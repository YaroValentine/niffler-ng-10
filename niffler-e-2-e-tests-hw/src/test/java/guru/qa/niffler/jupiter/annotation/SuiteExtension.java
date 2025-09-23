package guru.qa.niffler.jupiter.annotation;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {

  /*
  1. SuiteExtension runs before every test class.
  2. Any code that runs before the loading of the first test class is a beforeSuite().
  3. Any following test classes will not have the beforeSuite() method run.
  4. When all tests will end, the afterSuite() method will run.

  - The following lambda function puts an object that implements ClosableResource into the global store
  - in which the overridden method close runs afterSuite when tests are finished and jUnit closes rootContext
  */
  @Override
  default void beforeAll(ExtensionContext context) throws Exception {
    final ExtensionContext rootContext = context.getRoot();
    rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
        .getOrComputeIfAbsent(
            this.getClass(),
            key -> { // runs only on the first time
              beforeSuite(rootContext);
              return new ExtensionContext.Store.CloseableResource() {
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
