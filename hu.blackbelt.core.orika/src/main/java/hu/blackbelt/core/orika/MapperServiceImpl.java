package hu.blackbelt.core.orika;


import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.osgi.framework.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component(immediate = true, service = MapperService.class)
public class MapperServiceImpl implements MapperService, SynchronousBundleListener {

    private Logger log = LoggerFactory.getLogger(MapperServiceImpl.class);

    private volatile MapperFactory mapperFactory = null;
    private volatile BundleJavassistCompilerStrategy compilerStrategy = null;

    private Set<MapperRegistration> mapperRegistrations = new HashSet<>();

    private Object sync = new Object();

    @Activate
    protected void activate(BundleContext context) {
        context.addBundleListener(this);
        reCreateMapping();
    }

    @Deactivate
    protected void deactivate(BundleContext context) {
        context.removeBundleListener(this);
    }

    public void registerMap(Class src, Class dest) {
        registerMap(src, dest, null);
    }

    public void registerMap(Class src, Class dest, Map<String, String> fieldMap) {
        synchronized (sync) {
            MapperRegistration reg = new MapperRegistration(src, dest, fieldMap);
            if (mapperRegistrations.contains(reg)) {
                log.warn("Mapper have been registered already for: " + src.getName() + " -> " + dest.getName());
                //throw new RuntimeException("Mapper have been registered already for: "+src.getName()+" -> "+dest.getName());
            }
            mapperRegistrations.add(reg);
            registerMapInOrika(src, dest, fieldMap);
        }
    }

    public <A, B> void registerMapping(final CustomTransformer<A, B> transformer) {
        synchronized (sync) {
            MapperRegistration reg = new MapperRegistration(transformer.getAType(), transformer.getBType(), transformer);
            if (mapperRegistrations.contains(reg)) {
                log.warn("Mapper have been registered already for: "+transformer.getAType().getName()+" -> "+transformer.getBType().getName());
                //throw new RuntimeException("Mapper have been registered already for: "+transformer.getAType().getName()+" -> "+transformer.getBType().getName());
            }
            mapperRegistrations.add(reg);
        }
        registerTransformerInOrika(transformer);
    }

    public Mapper getMapper() {
        synchronized (sync) {
            return new MapperFacedeDelegatorMapper(this);
        }
    }

    @Override
    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.STOPPING) {
            removeBundleFromMappers(event.getBundle());
        }
    }

    protected MapperFactory getMapperFactory() {
        synchronized (sync) {
            return this.mapperFactory;
        }
    }
    private void removeBundleFromMappers(Bundle bundle) {
        // Search for it is used or not in mapping regstrations
        boolean haveToReload = false;
        synchronized (sync) {
            for (MapperRegistration mapperRegistration : new HashSet<MapperRegistration>(mapperRegistrations)) {
                if (FrameworkUtil.getBundle(mapperRegistration.getSrc()).equals(bundle) ||
                        FrameworkUtil.getBundle(mapperRegistration.getDest()).equals(bundle)) {
                    log.info("Unregistering mapper for: " + mapperRegistration.getSrc().getName() + " -> " + mapperRegistration.getDest().getName());

                    mapperRegistrations.remove(mapperRegistration);
                    haveToReload = true;
                }
            }
        }
        if (haveToReload) {
            reCreateMapping();
        }
    }

    private void registerMapInOrika(Class src, Class dest, Map<String, String> fieldMap) {
        log.info("Registering mapper for: "+src.getName()+" -> "+dest.getName());

        prepareClassLoader(src, dest);

        if (fieldMap != null) {
            ClassMapBuilder builder = mapperFactory.classMap(src, dest);
            for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
                builder.field(entry.getKey(), entry.getValue());
            }
            mapperFactory.registerClassMap(builder.byDefault().toClassMap());
        } else {
            mapperFactory.registerClassMap(mapperFactory.classMap(src, dest).byDefault().toClassMap());
        }
    }

    private <A, B> void registerTransformerInOrika(final CustomTransformer<A, B> transformer) {

        final Class<A> aType = transformer.getAType();
        final Class<B> bType = transformer.getBType();
        log.info("Registering transformer for: "+aType+" -> "+bType);

        prepareClassLoader(aType, bType);

        mapperFactory.registerClassMap(mapperFactory.classMap(aType, bType).byDefault().customize(new CustomTransformerAdapter<>(transformer)).toClassMap());
    }

    private void prepareClassLoader(Class src, Class dest) {
        compilerStrategy.registerClassLoader(src.getClassLoader());
        compilerStrategy.registerClassLoader(dest.getClassLoader());
    }

    private void reCreateMapping() {
        synchronized (sync) {
            log.debug("Recreating orika mapping");

            try {
                compilerStrategy = new BundleJavassistCompilerStrategy();
            } catch (Throwable th) {
                log.error("reCreateMapping() - Could not load Orika mapper - {}", th.getLocalizedMessage());
                log.debug("Exception: ",th);
            }

            mapperFactory = new DefaultMapperFactory.Builder()
                    .compilerStrategy(compilerStrategy)
                    .useAutoMapping(true)
                    .build();

            mapperFactory.getConverterFactory().registerConverter(new YesNoConverter());

            // Setup already registered mappings and transformers
            for (MapperRegistration mapperRegistration : mapperRegistrations) {
                if (mapperRegistration.getCustomTransformer() == null) {
                    registerMapInOrika(mapperRegistration.getSrc(), mapperRegistration.getDest(), mapperRegistration.getFieldMap());
                } else {
                    registerTransformerInOrika(mapperRegistration.getCustomTransformer());
                }
            }
        }
    }
}
