package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.RandomDataUtils;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CreateCategoryExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CreateCategoryExtension.class);

  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    // Set Up:
    // Add random category before test
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {
            Category categoryAnno = userAnno.categories()[0];
            CategoryJson category = new CategoryJson(
                null,
                RandomDataUtils.randomCategoryName(),
                userAnno.username(),
                categoryAnno.archived()
            );

            CategoryJson created = spendApiClient.addCategory(category);
            if (categoryAnno.archived()) {
              CategoryJson archivedCategory = new CategoryJson(
                  created.id(),
                  created.name(),
                  created.username(),
                  true
              );
              created = spendApiClient.updateCategory(archivedCategory);
            }

            context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                created
            );
          }
        });
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    // Clean Up:
    // archive category after test if not archived
    CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
    if (category != null && !category.archived()) {
      CategoryJson updatedCategory = new CategoryJson(
          category.id(),
          category.name(),
          category.username(),
          true
      );
      spendApiClient.updateCategory(updatedCategory);
    }
  }


  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}
