package com.nie.common.tools;

import com.nie.feign.client.DoctorClient;
import com.nie.feign.client.PatientClient;
import com.nie.feign.dto.Doctor;
import com.nie.feign.dto.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final PatientClient patientClient;
    private final DoctorClient doctorClient;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final Patient patient = patientClient.securityP(Integer.valueOf(username));
        if (patient != null) {
            log.info("用户名：{}", patient);
            return createUserDetails(patient.getPId(), patient.getPPassword(), "patient");
        }
        final Doctor doctor = doctorClient.securityD(Integer.valueOf(username));
        if (doctor != null) {
            log.info("用户名：{}", doctor);
            return createUserDetails(doctor.getdId(), doctor.getdPassword(), "doctor");
        }
        throw new UsernameNotFoundException("用户不存在");
    }

    UserDetails createUserDetails(Integer username, String password, String role) {
        password = bCryptPasswordEncoder.encode(password);
        return User.builder()
                .username(String.valueOf(username))
                .password(password)
                .roles(role)
                .build();
    }
}
