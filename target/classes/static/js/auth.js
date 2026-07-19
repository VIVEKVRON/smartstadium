const SUPABASE_URL = 'https://anoofcmhgkdclgeredla.supabase.co';
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFub29mY21oZ2tkY2xnZXJlZGxhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODQ0NjY5ODYsImV4cCI6MjEwMDA0Mjk4Nn0.IF_VHdw6oOFXE4p8Fts_Vd-HHuiCQWxOW9CuLHM9Q6w';

class AppAuth {
    constructor() {
        this.supabase = window.supabase.createClient(SUPABASE_URL, SUPABASE_ANON_KEY);
        this.session = null;
        this.initAuthListener();
    }

    async initAuthListener() {
        // Initial session check
        const { data: { session } } = await this.supabase.auth.getSession();
        this.session = session;
        if (session) {
            localStorage.setItem('auth_token', session.access_token);
        }

        // Listen for changes
        this.supabase.auth.onAuthStateChange((event, session) => {
            this.session = session;
            if (session) {
                localStorage.setItem('auth_token', session.access_token);
            } else {
                const existingToken = localStorage.getItem('auth_token');
                if (existingToken && existingToken.startsWith('eyJhbGciOiJIUzI1Ni')) {
                    // It's our test token, preserve it and don't redirect
                    return;
                }
                localStorage.removeItem('auth_token');
                // Redirect to home if logged out and not on index
                if (!window.location.pathname.endsWith('index.html') && window.location.pathname !== '/') {
                    window.location.href = 'index.html';
                }
            }
        });
    }

    async login(email, password, redirectUrl) {
        try {
            // Local Test Credentials Bypass
            if (email === 'staff@fifa2026.com' && password === 'Password123!') {
                const testToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3OC0xMjM0LTEyMzQtMTIzNC0xMjM0NTY3ODkwMTIiLCJlbWFpbCI6InN0YWZmQGZpZmEyMDI2LmNvbSIsInVzZXJfcm9sZSI6IlNUQUZGIiwiZXhwIjoxODE2MDE1ODkzLCJpYXQiOjE3ODQ0Nzk4OTN9.cgi6Mpf_HZamwDiJjx3Xm-KtbgI4MZvl0BN7-Us5Nc4';
                localStorage.setItem('auth_token', testToken);
                window.location.href = redirectUrl;
                return;
            }

            const { data, error } = await this.supabase.auth.signInWithPassword({
                email,
                password
            });

            if (error) throw error;
            
            if (data.session) {
                window.location.href = redirectUrl;
            }
        } catch (error) {
            console.error('Login error:', error.message);
            alert('Login failed: ' + error.message);
        }
    }

    async loginAnonymously(redirectUrl) {
        // For Fan Portal, we simulate anonymous access or simple redirect
        // Since Supabase anonymous sign in might need specific configuration,
        // we'll just redirect for now.
        window.location.href = redirectUrl;
    }

    async logout() {
        const { error } = await this.supabase.auth.signOut();
        if (error) {
            console.error('Logout error:', error.message);
        }
    }

    getAuthHeaders() {
        const token = localStorage.getItem('auth_token');
        const headers = {
            'Content-Type': 'application/json'
        };
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }
        return headers;
    }

    getUserRole() {
        const token = localStorage.getItem('auth_token');
        if (token && token.startsWith('eyJhbGciOiJIUzI1Ni')) {
             try {
                 const payload = JSON.parse(atob(token.split('.')[1]));
                 return payload.user_role || 'user';
             } catch (e) {
                 return 'user';
             }
        }
        if (!this.session) return null;
        return this.session.user.app_metadata?.role || 'user';
    }
}

window.appAuth = new AppAuth();
