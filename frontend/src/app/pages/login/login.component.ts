import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
    loginForm!: FormGroup;
    showPassword = false;

    errorMessage: string = '';

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.loginForm = this.fb.group({
            username: ['', Validators.required],
            password: ['', Validators.required],
            authMode: ['local', Validators.required]
        });
    }

    togglePasswordVisibility(): void {
        this.showPassword = !this.showPassword;
    }

    onSubmit(): void {
        if (this.loginForm.valid) {
            this.errorMessage = '';
            const { username, password, authMode } = this.loginForm.value;
            
            this.authService.login(username, password, authMode).subscribe({
                next: () => {
                    this.router.navigate(['/dashboard']);
                },
                error: (err) => {
                    console.error('Login failed', err);
                    this.errorMessage = 'Échec de la connexion. Veuillez vérifier vos identifiants.';
                }
            });
        } else {
            this.loginForm.markAllAsTouched();
        }
    }
}
