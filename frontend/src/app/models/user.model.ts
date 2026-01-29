export interface User {
    username: string;
    password?: string;
    enabled: boolean;
    roles: string[];
}
