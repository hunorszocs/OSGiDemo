package hu.blackbelt.core.orika;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * this guy converts between Y/N in the db and booleans in java
 *
 * @author Akos Mocsanyi
 */
public final class YesNoConverter extends BidirectionalConverter<Boolean, String> {
    protected static final String NO = "N";
    protected static final String YES = "Y";

    @Override
    public String convertTo(Boolean source, Type<String> destinationType) {
        if (Boolean.TRUE.equals(source)) {
            return YES;
        } else {
            return NO;
        }
    }

    @Override
    public Boolean convertFrom(String source, Type<Boolean> destinationType) {
        return YES.equalsIgnoreCase(source);
    }

    /**
     * ugly hack to get around a probable bug in orika's {@link ma.glasnost.orika.CustomConverter}
     *
     * @return
     */
    @Override
    public BidirectionalConverter<String, Boolean> reverse() {
        final BidirectionalConverter<String, Boolean> reversed = super.reverse();

        return new BidirectionalConverter<String, Boolean>() {
            @Override
            public Boolean convertTo(String source, Type<Boolean> destinationType) {
                return reversed.convertTo(source, destinationType);
            }

            @Override
            public String convertFrom(Boolean source, Type<String> destinationType) {
                return reversed.convertFrom(source, destinationType);
            }
        };

    }
}
