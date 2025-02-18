package api.giybat.uz.dto;

import api.giybat.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;

import java.util.List;

public class JwtDTO {

    private Integer id;
    private List<ProfileRole> roleList;
    private String username;

    public JwtDTO( String username, Integer id, List<ProfileRole> roleList) {
        this.id = id;
        this.roleList = roleList;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ProfileRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<ProfileRole> roleList) {
        this.roleList = roleList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
