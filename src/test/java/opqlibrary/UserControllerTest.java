package opqlibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerUser_success() throws Exception {
        String json = "{\"username\":\"newuser\",\"password\":\"password123\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    void registerUser_duplicateUsername() throws Exception {
        String json = "{\"username\":\"dupeuser\",\"password\":\"password123\"}";
        // First registration should succeed
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        // Second registration should fail
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void login_success() throws Exception {
        // Register user first
        String json = "{\"username\":\"loginuser\",\"password\":\"Password1!\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        // Login
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "loginuser")
                .param("password", "Password1!"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void login_failure() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "nouser")
                .param("password", "badpass"))
                .andExpect(status().is3xxRedirection()); // Expect redirect to /login?error
    }

    @Test
    void rememberMe_setsCookie() throws Exception {
        // Register user first
        String json = "{\"username\":\"remembermeuser\",\"password\":\"Password1!\"}";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
        // Login with remember-me
        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "remembermeuser")
                .param("password", "Password1!" )
                .param("remember-me", "on"))
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("remember-me"));
    }

    @Test
    void protectedEndpoint_requiresAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books"))
                .andExpect(status().isUnauthorized()); // Should return 401
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void protectedEndpoint_withAuth() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/books"))
                .andExpect(status().isOk());
    }
} 