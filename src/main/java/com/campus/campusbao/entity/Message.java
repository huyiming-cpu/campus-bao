package com.campus.campusbao.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer fromuserid;
    private Integer touserid;
    private String content;
    private Date createtime;
    private Integer isread;
    private Integer productid;

    // 必须写 getter 和 setter！！！
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getFromuserid() { return fromuserid; }
    public void setFromuserid(Integer fromuserid) { this.fromuserid = fromuserid; }

    public Integer getTouserid() { return touserid; }
    public void setTouserid(Integer touserid) { this.touserid = touserid; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatetime() { return createtime; }
    public void setCreatetime(Date createtime) { this.createtime = createtime; }

    public Integer getIsread() { return isread; }
    public void setIsread(Integer isread) { this.isread = isread; }

    public Integer getProductid() { return productid; }
    public void setProductid(Integer productid) { this.productid = productid; }
}