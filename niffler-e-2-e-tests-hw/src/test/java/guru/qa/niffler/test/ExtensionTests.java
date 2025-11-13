package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ExtensionTests {

  @Test
  void extensionContextTest() {
    ExtensionContext ctx = TestMethodContextExtension.context();
    System.out.println("");
  }


}
