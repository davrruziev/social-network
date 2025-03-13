package api.giybat.uz.repository;

import api.giybat.uz.entity.SmsHistoryEntity;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity, String> {

    /// select count(*) from sms_history where phone = ? and created_date between ? and ?

    Long countByPhoneAndCreatedDateBetween(String phone, LocalDateTime from, LocalDateTime to);

   // select * from sms_history where phone =? order by created_date desc limit 1

    Optional<SmsHistoryEntity> findTop1ByPhoneOrderByCreatedDateDesc(String phone);
}
