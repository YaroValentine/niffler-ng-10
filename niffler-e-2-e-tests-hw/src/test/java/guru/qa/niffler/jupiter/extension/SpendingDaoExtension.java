package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.UserDb;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.db.SpendDbClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingDaoExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingDaoExtension.class);

  private final SpendDbClient spendDbClient = new SpendDbClient();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), UserDb.class)
      .ifPresent(userAnno -> {
        if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
          Spending spendAnno = userAnno.spendings()[0];

          // Try to reuse a category created by CategoryDaoExtension
          CategoryJson generatedCategory = context
            .getStore(CategoryDaoExtension.NAMESPACE)
            .get(context.getUniqueId(), CategoryJson.class);

          CategoryJson categoryForSpend = generatedCategory != null
            ? generatedCategory
            : new CategoryJson(
            null,
            spendAnno.category(),
            userAnno.username(),
            false
          );

          SpendJson spend = new SpendJson(
            null,
            new Date(),
            categoryForSpend,
            CurrencyValues.RUB,
            spendAnno.amount(),
            spendAnno.description(),
            userAnno.username()
          );

          context.getStore(NAMESPACE).put(
            context.getUniqueId(),
            spendDbClient.createSpend(spend)
          );
        }
      });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
    return context.getStore(SpendingDaoExtension.NAMESPACE).get(context.getUniqueId(), SpendJson.class);
  }
}
