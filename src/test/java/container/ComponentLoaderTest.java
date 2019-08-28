package container;

import io.vertx.core.Verticle;
import me.wang007.annotation.Deploy;
import me.wang007.example.DemoVerticle;
import org.junit.Assert;
import org.junit.Test;
import me.wang007.container.Component;
import me.wang007.container.DefaultComponentLoader;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * created by wang007 on 2019/2/26
 */
public class ComponentLoaderTest {


    @Test
    public void createTest() {
        DefaultComponentLoader loader = new DefaultComponentLoader();
        Component component = loader.createComponent(ArrayList.class);
        System.out.println(component);
    }


    @Test
    public void loadTest() {
        DefaultComponentLoader loader = new DefaultComponentLoader();

        HashSet<Class<?>> set = new HashSet<>();
        set.add(DemoVerticle.class);

        List<Class<? extends Annotation>> ans = new ArrayList<>();
        ans.add(Deploy.class);

        Map<Class<?>, Component> map = loader.loadComponents(set, ans);
        Component component = map.get(DemoVerticle.class);
        Assert.assertEquals(DemoVerticle.class, component.getClazz());
    }


    @Test
    public void loadTest1() {
        DefaultComponentLoader loader = new DefaultComponentLoader();

        HashSet<Class<?>> set = new HashSet<>();
        set.add(DemoVerticle.class);

        List<Class<?>> target = new ArrayList<>();
        target.add(DemoVerticle.class);
        Map<Class<?>, Component> map = loader.loadComponents(set, Collections.emptyList(), target, Collections.emptySet());

        Component component = map.get(DemoVerticle.class);
        Assert.assertEquals(DemoVerticle.class, component.getClazz());
    }


    @Test
    public void loadTest2() {

        DefaultComponentLoader loader = new DefaultComponentLoader();

        HashSet<Class<?>> set = new HashSet<>();
        set.add(DemoVerticle.class);

        Map<Class<?>, Component> map = loader.loadComponents(set, Collections.singleton(Verticle.class));
        Component component = map.get(DemoVerticle.class);
        Assert.assertEquals(DemoVerticle.class, component.getClazz());
    }








}
