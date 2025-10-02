package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.*;

@ExtendWith(UsersQueueExtension.class)
public class QuickTest {

  @Test
  @EnabledIfSystemProperty(named = "env", matches = "remote")
  void name() {

  }

  //  private static final Config CFG = Config.getInstance();
//  private final SpendApiClient spendApiClient = new SpendApiClient();






}
