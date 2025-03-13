package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsProviderTokenEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsProviderTokenRepository extends CrudRepository<SmsProviderTokenEntity, Integer> {
    Optional<SmsProviderTokenEntity> findTop1By();
}
