package sg.edu.np.mad.travelhub;

public class User {
    private String uid;
    String imageUrl, id, name, description, email, password;
    int followers, following;
    public User() {}

    public User(String imageUrl, String name, String description, String email, String password, String id, String uid) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.name = name;
        this.description = description;
        this.email = email;
        this.password = password;
        this.uid = uid;
        //this.followers = followers;
        //this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getUid() {return uid;}

    //public int getFollowers() {return followers;}
    //public int getFollowing() {return following;}

    public void setId(String id) {
        this.id = id;
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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    //public void followUser(User user) {}
}
