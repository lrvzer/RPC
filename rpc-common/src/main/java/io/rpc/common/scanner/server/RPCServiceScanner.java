package io.rpc.common.scanner.server;

import io.rpc.annotation.RPCService;
import io.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @RPCService注解扫描器
 */
public class RPCServiceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RPCServiceScanner.class);

    public static Map<String, Object> doScannerWithRPCServiceAnnotationFilterAndRegistryService(/*String host,
                                                                                                int port,*/
                                                                                                String scanPackage
            /*,RegistryService registryService*/) throws Exception {
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty())
            return handlerMap;
        classNameList.forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                RPCService rpcService = clazz.getAnnotation(RPCService.class);
                // 处理注解信息
                if (rpcService != null) {
                    // 对于@RPCService中的配置，优先使用interfaceClass，如果interfaceClass为空，再使用interfaceClassName
                    // TODO 处理后续逻辑
                    logger.info("当前标注了 @RpcService 注解的类实例名称 ===>>> {}", clazz.getName());
                    logger.info("@RpcService 注解上标注的属性信息如下：");
                    logger.info("interfaceClass: {}", rpcService.interfaceClass().getName());
                    logger.info("interfaceClassName: {}", rpcService.interfaceClassName());
                    logger.info("version: {}", rpcService.version());
                    logger.info("group: {}", rpcService.group());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception: {0}", e);
            }
        });
        return handlerMap;
    }
}
