package guru.qa.niffler.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

  /*
    1) Be sure that SuiteExtension will run before each test class
    2) If we execute some code before loading the very first test class, that will be beforeSuite().
    3) For the 2nd, 3rd, and so on (up to N) test classes, we will no longer call beforeSuite()
    4) When absolutely all tests are finished, we will call afterSuite()
   */
  @Override
  default void beforeAll(ExtensionContext context) throws Exception {
    final ExtensionContext rootContext = context.getRoot();
    rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
        .getOrComputeIfAbsent(
            this.getClass(),
            key -> {
              beforeSuite(rootContext);
              return new AutoCloseable() {
                @Override
                public void close() {
                  afterSuite();
                }
              };
            }
        );
  }

  default void beforeSuite(ExtensionContext context) {
  }

  default void afterSuite() {
  }
}
