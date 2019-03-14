package me.wang007.boot;


import java.util.Map;
import java.util.function.Consumer;

/**
 * vertxBoot
 *
 * created by wang007 on 2019/3/14
 */
public interface VertxBootWithHook extends VertxBoot, BootHooks {

    @Override
    VertxBootWithHook afterLoadPropertiesHook(Consumer<Map<String, String>> hook);

    @Override
    VertxBootWithHook beforeLoadComponentsHook(Consumer<VertxBoot> hook);

    @Override
    VertxBootWithHook afterLoadComponentsHook(Consumer<VertxBoot> hook);

    @Override
    VertxBootWithHook beforeDeployedHook(Consumer<VertxBoot> hook);

    @Override
    VertxBootWithHook afterDeployedHook(Consumer<VertxBoot> hook);

    @Override
    VertxBootWithHook afterStartHook(Runnable hook);
}
