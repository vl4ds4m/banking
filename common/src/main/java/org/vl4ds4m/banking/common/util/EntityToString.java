package org.vl4ds4m.banking.common.util;

class EntityToString {

    private EntityToString() {}

    static String string(Class<?> cls, Object... args) {
        var sb = new StringBuilder(cls.getSimpleName());

        if (args.length > 0) {
            sb.append("[");
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(args[i]);
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
