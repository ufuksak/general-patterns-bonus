import com.aurea.data.UTAClass;
import com.aurea.data.UTAMethod;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

public class IntegrationTest {

    @Test
    public void test()  {
        TestGenerator testGenerator = new TestGenerator();

        UTAClass classToTest = new UTAClass();
        UTAMethod methodToTest = new UTAMethod();

        UTAMethod resultMethod = testGenerator.generateTestMethodsFor(classToTest, methodToTest);

        UTAMethod expectedResultMethod = new UTAMethod();

        String expected =
                "@Test" +
                "public void test_SimpleClassWithPureMethods_addNumbers() {" +
                        "assertTrue(5, addNumbers(3,2));" +
                        "assertTrue(6, addNumbers(3,3));" +
                        "assertTrue(7, addNumbers(0,7));" +
                        "}";

        expectedResultMethod.buildFromString(expected);

        assertTrue(expectedResultMethod.equals(resultMethod));
    }

}
