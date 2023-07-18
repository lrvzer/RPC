package io.rpc.provider.common.scanner;

import io.rpc.annotation.RPCService;
import io.rpc.common.helper.RPCServiceHelper;
import io.rpc.common.scanner.ClassScanner;
import io.rpc.protocol.meta.ServiceMeta;
import io.rpc.registry.api.RegistryService;
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

    public static Map<String, Object> doScannerWithRPCServiceAnnotationFilterAndRegistryService(String host,
                                                                                                int port,
                                                                                                String scanPackage,
                                                                                                RegistryService registryService
    ) throws Exception {
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
                    ServiceMeta serviceMeta = new ServiceMeta(getServiceName(rpcService), rpcService.version(), host, port, rpcService.group());
                    // 将元数据注册到注册中心
                    registryService.registry(serviceMeta);

                    String key =
                            RPCServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
                    handlerMap.put(key, clazz.newInstance());
                }
            } catch (Exception e) {
                logger.error("scan classes throws exception: {0}", e);
            }
        });
        return handlerMap;
    }

    private static String getServiceName(RPCService rpcService) {
        return rpcService.interfaceClassName();
    }
}
