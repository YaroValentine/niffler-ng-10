package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryDaoExtension;
import guru.qa.niffler.jupiter.extension.SpendingDaoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({
  CategoryDaoExtension.class,
  SpendingDaoExtension.class
})
public @interface UserDb {

  String username();

  Category[] categories() default {};

  Spending[] spendings() default {};
}
