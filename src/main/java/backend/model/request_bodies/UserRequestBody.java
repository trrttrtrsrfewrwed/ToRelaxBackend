package backend.model.request_bodies;


public class UserRequestBody {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserRequestBody(long id, String name, String email, String password, String data) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setToken(String token) {this.token = token;}
    public String getToken() { return token; }

    private long id;
    private String name;
    private String email;
    private String password;
    private String data;
    private String token;
}
