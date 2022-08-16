namespace TopCoder.Server.Controller {

    sealed class JavaUtils {

        JavaUtils() {
        }

        static long Convert(long v, int lim) {
            long r=0;
            for (int i=0; i<=lim; i++) {
                long b=((v>>i*8) & 0xff) << 8*(lim-i);
                r+=b;
            }
            return r;
        }

        static internal long ConvertInt64(long v) {
            return Convert(v,7);
        }

        static internal int ConvertInt32(int v) {
            return (int) Convert(v,3);
        }

        static internal short ConvertInt16(short v) {
            return (short) Convert(v,1);
        }

    }

}
