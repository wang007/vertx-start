package boot;

import me.wang007.boot.PropertiesLoader;
import me.wang007.constant.PropertyConst;
import me.wang007.container.DefaultContainer;
import me.wang007.example.Profile;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * created by wang007 on 2019/2/27
 */
public class PropertiesLoaderTest {


    @Test
    public void loadPropertiesTest() {

        DefaultContainer container = new DefaultContainer();
        PropertiesLoader propertiesLoader = new PropertiesLoader(container);

        Map<String, String> map = propertiesLoader.loadProperties(PropertyConst.Default_Properties_Path);
        Assert.assertEquals(map.get("name"), "wang007");
    }


    @Test
    public void loadForTest() {

        DefaultContainer container = new DefaultContainer();
        PropertiesLoader propertiesLoader = new PropertiesLoader(container);
        container.start("me.wang007");

        Map<String, String> map = propertiesLoader.loadProperties(PropertyConst.Default_Properties_Path);

        Profile profile = propertiesLoader.loadFor(Profile.class, map);
        Assert.assertEquals(profile.getName(), "wang007");
    }

}
