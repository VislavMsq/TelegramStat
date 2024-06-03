package stb.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "otp")
public class OTP {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "otp")
    private Long otp;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name")
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OTP otp1 = (OTP) o;
        return Objects.equals(id, otp1.id) && Objects.equals(otp, otp1.otp) && Objects.equals(userId, otp1.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, otp, userId);
    }

    @Override
    public String toString() {
        return "OTP{" +
                "id=" + id +
                ", otp=" + otp +
                ", name='" + name +
                ", userId=" + userId +
                '}';
    }
}
