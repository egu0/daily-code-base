import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

interface Task {
    Target process();
}

interface Target {
    String describe();
}

public class NewFeatureTests {
    @Test
    public void testNewFeature() {
        var starter = new Starter();
        assertEquals("a cute corgi ...", starter.call());
    }
}

class Starter {

    HashMap<String, Task> map = new HashMap<>();

    public Starter() {
        map.put("b", this::generate);
        // 特性：将方法名作为 key
        // 要求：
        // 1. Task 必须是函数式接口，即其中只能有一个抽象定义
        // 2. generate() 返回值类型 与 Task的抽象接口返回值 相同

    }

    public String call() {
        Target i = map.get("b").process();
        return i.describe();
    }

    public Target generate() {
        return new TargetType1();
    }
}

class TargetType1 implements Target {
    @Override
    public String describe() {
        return "a cute corgi ...";
    }
}
