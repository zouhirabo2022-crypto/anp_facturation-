import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { map, take } from 'rxjs';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const required: string[] = (route.data && (route.data['roles'] as string[])) || [];

  if (!required.length) {
    return true;
  }

  return authService.hasAnyRole(required).pipe(
    take(1),
    map(has => {
      if (has) return true;
      router.navigate(['/dashboard']);
      return false;
    })
  );
};
