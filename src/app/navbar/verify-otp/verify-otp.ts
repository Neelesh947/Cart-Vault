import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthProxyService } from '../../services/auth-proxy/auth-proxy-service';

@Component({
  selector: 'app-verify-otp',
  standalone: false,
  templateUrl: './verify-otp.html',
  styleUrl: './verify-otp.css'
})
export class VerifyOtp implements OnInit {

  otpForm!: FormGroup;
  userId!: string;

  constructor(private route: ActivatedRoute, private fb: FormBuilder, private authService: AuthProxyService, private router: Router) { }

  ngOnInit(): void {
    this.userId = this.route.snapshot.paramMap.get('userId')!;
    this.otpForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  verifyUserOtp() {
    const otp = this.otpForm.value.otp;
    this.authService.verifyOtp(this.userId, otp).subscribe({
      next: () => {
        alert('OTP verified and user enabled successfully');
        this.router.navigate(['/login']);
      },
      error: err => {
        console.error('OTP verification failed:', err);
        alert('Verification failed: ' + (err.error || 'Unknown error'));
      }
    });
  }

}
