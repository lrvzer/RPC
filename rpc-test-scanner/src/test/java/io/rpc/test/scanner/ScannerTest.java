package io.rpc.test.scanner;

import io.rpc.common.scanner.ClassScanner;
import io.rpc.common.scanner.server.RPCReferenceScanner;
import io.rpc.common.scanner.server.RPCServiceScanner;
import org.junit.Test;

import java.util.List;

public class ScannerTest {

    @Test
    public void testScannerClassNameList() throws Exception {
        List<String> classNameList = ClassScanner.getClassNameList("io.rpc.test.scanner");
        classNameList.forEach(System.out::println);
    }

    @Test
    public void testScannerClassNameListByRPCService() throws Exception {
        RPCServiceScanner.doScannerWithRPCServiceAnnotationFilterAndRegistryService("io.rpc.test.scanner");
    }

    @Test
    public void testScannerClassNameListByRPCReference() throws Exception {
        RPCReferenceScanner.doScannerWithRPCReferenceAnnotationFilterAndRegistryService("io.rpc.test.scanner");
    }
}
