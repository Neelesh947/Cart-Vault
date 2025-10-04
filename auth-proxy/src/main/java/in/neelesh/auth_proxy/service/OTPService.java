package in.neelesh.auth_proxy.service;

import java.util.Random;

import org.infinispan.client.hotrod.RemoteCache;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OTPService {

	private final RemoteCache<String, String> otpCache;

	public String generateOtp(String userId) {
		String otp = String.valueOf(100_000 + new Random().nextInt(900_000));
		otpCache.put(userId, otp);
		return otp;
	}

	public boolean verifyOtp(String userId, String otp) {
		String cachedOtp = otpCache.get(userId);
		if (otp.equals(cachedOtp)) {
			otpCache.remove(userId);
			return true;
		}
		return false;
	}
}
