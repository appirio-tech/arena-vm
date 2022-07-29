namespace TopCoder.Server.Controller {

    enum ObjectType: byte {

        Null=1,
        String=2,
        Boolean=3,
        Integer=4,
        Double=6,
        ByteArray=8,
        ObjectArray=9,
        IntArray=10,
        Char=11,
        StringArray=12,
        Long=13,
        ObjectArrayArray=14,
        DoubleArray=16,
        LongArray=19,
        Hashtable=34,

        CompileRequest=97,
        CompileResponse=98,
        TestRequest=99,
        TestResponse=100,
        SystemTestRequest=101,
        SystemTestResponse=102,
        PracticeSystemTestRequest=103,
        PracticeSystemTestResponse=104,
        RestartServiceRequest=105,

    }

}
