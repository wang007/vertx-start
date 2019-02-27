package container;

import io.vertx.core.Verticle;
import me.wang007.annotation.Deploy;
import me.wang007.annotation.Properties;
import me.wang007.annotation.Route;
import me.wang007.container.Component;
import me.wang007.container.DefaultContainer;
import me.wang007.example.DemoVerticle;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * created by wang007 on 2019/2/27
 */
public class ContainerTest {


    @Test
    public void containerTest() {

        DefaultContainer container = new DefaultContainer();
        container.registerLoadBy(Deploy.class).registerLoadBy(Route.class).registerLoadBy(Properties.class);
        container.start("me.wang007");

        List<Component> components = container.getComponentsByAnnotation(Deploy.class);
        Assert.assertTrue(components.size() != 0);
    }


    @Test
    public void containerTest1() {

        DefaultContainer container = new DefaultContainer();
        container.registerLoadBy(Deploy.class).registerLoadBy(Route.class).registerLoadBy(Properties.class);
        container.start("me.wang007");

        Component component = container.getComponent(DemoVerticle.class);
        Assert.assertTrue(component != null);
    }

    @Test
    public void containerTest2() {

        DefaultContainer container = new DefaultContainer();
        container.registerLoadBy(Deploy.class).registerLoadBy(Route.class).registerLoadBy(Properties.class);
        container.start("me.wang007");

        List<Component> components = container.getComponentsFrom(Verticle.class);
        Assert.assertTrue(components.size() != 0);

    }

}
