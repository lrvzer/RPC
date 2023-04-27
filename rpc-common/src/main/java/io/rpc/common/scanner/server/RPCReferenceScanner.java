package io.rpc.common.scanner.server;

import io.rpc.annotation.RPCReference;
import io.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class RPCReferenceScanner extends ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(RPCReferenceScanner.class);

    public static Map<String, Object> doScannerWithRPCReferenceAnnotationFilterAndRegistryService(/*String host,
                                                                                                  int port,*/
                                                                                                  String scanPackage
            /*,RegistryService registryService*/) throws Exception {
        // TODO 处理后续逻辑
        Map<String, Object> handlerMap = new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList.isEmpty())
            return handlerMap;
        classNameList.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach(field -> {
                    RPCReference rpcReference = field.getAnnotation(RPCReference.class);
                    if (rpcReference != null) {
                        // TODO 处理后续逻辑
                        logger.info("当前标注了 @RpcReference 注解的字段名称: {}", field.getName());
                        logger.info("@RpcReference 注解上标注的属性信息如下：");
                        logger.info("version: {}", rpcReference.version());
                        logger.info("group: {}", rpcReference.group());
                        logger.info("registryType: {}", rpcReference.registryType());
                        logger.info("registryAddress: {}", rpcReference.registryAddress());
                    }
                });
            } catch (ClassNotFoundException e) {
                logger.error("scan classes throws exception: {0}", e);
            }
        });
        return handlerMap;
    }
}
