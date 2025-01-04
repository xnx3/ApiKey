package cn.zvo.key.apiKeyCount.entity;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.xnx3.Lang;

/**
 * key 管理
 * @author <a href="https://github.com/xnx3/writecode">WriteCode自动生成</a>
 */
@Entity()
@Table(name = "key")
public class Key implements java.io.Serializable{

	private String key;	//API Key 
	private Integer addtime;	//添加时间 
	private Integer count;	//总次数 
	private Integer surplus;	//剩余次数 
	private String url;		//允许请求的API URL的接口，设置如 /a/b.json  省略掉域名部分
	
	public Key() {
		this.key = Lang.uuid()+Lang.uuid();
	}
	
	@Id
	@Column(name = "key", columnDefinition="char(64)")
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	@Column(name = "addtime", columnDefinition="int(11)")
	public Integer getAddtime() {
		return addtime;
	}
	public void setAddtime(Integer addtime) {
		this.addtime = addtime;
	}
	@Column(name = "count", columnDefinition="int(11)")
	public Integer getCount() {
		if(count == null) {
			return 0;
		}
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	@Column(name = "surplus", columnDefinition="int(11)")
	public Integer getSurplus() {
		return surplus;
	}
	public void setSurplus(Integer surplus) {
		this.surplus = surplus;
	}
	@Column(name = "url", columnDefinition="char(200)")
	public String getUrl() {
		if(url == null) {
			url = "";
		}
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "{key : "+this.key+", addtime : "+this.addtime+", count : "+this.count+", surplus : "+this.surplus+"}";
	}
}