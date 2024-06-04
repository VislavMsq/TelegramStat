package org.stb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stb.entity.OTP;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long>{
}
