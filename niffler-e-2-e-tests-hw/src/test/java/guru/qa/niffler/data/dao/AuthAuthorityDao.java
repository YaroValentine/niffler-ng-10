package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.util.List;
import java.util.UUID;

public interface AuthAuthorityDao {

  void create(AuthAuthorityEntity... authorities);

  List<AuthAuthorityEntity> findAllByUserId(UUID userId);

  void delete(AuthAuthorityEntity... authorities);
}

