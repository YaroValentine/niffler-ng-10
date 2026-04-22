package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.UserDb;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.db.CategoryDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryDaoExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryDaoExtension.class);

  private final CategoryDbClient categoryDbClient = new CategoryDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    // Set Up:
    // Add random category before test
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), UserDb.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {
            Category categoryAnno = userAnno.categories()[0];
            CategoryJson category = new CategoryJson(
                null,
                RandomDataUtils.randomCategoryName(),
                userAnno.username(),
                categoryAnno.archived()
            );

            // DB allows creating category with archived status directly (no business logic)
            CategoryJson created = categoryDbClient.createCategory(category);

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
      categoryDbClient.updateCategory(updatedCategory);
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
