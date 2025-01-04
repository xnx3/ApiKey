package cn.zvo.key.apiKeyCount.vo;

import cn.zvo.key.apiKeyCount.entity.Key;
import com.xnx3.j2ee.system.responseBody.ResponseBodyManage;
import com.xnx3.j2ee.vo.BaseVO;

/**
 * key 详情
 * @author <a href="https://github.com/xnx3/writecode">WriteCode自动生成</a>
 */
@ResponseBodyManage(ignoreField = {}, nullSetDefaultValue = true)
public class KeyVO extends BaseVO {
	private Key key;	// key
	
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}
	
	@Override
	public String toString() {
		return "KeyVO [key=" + key + "]";
	}
	
}
