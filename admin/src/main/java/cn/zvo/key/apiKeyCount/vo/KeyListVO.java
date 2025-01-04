package cn.zvo.key.apiKeyCount.vo;

import java.util.List;
import cn.zvo.key.apiKeyCount.entity.Key;
import com.xnx3.j2ee.system.responseBody.ResponseBodyManage;
import com.xnx3.j2ee.util.Page;
import com.xnx3.j2ee.vo.BaseVO;

/**
 * key 列表
 * @author <a href="https://github.com/xnx3/writecode">WriteCode自动生成</a>
 */
@ResponseBodyManage(ignoreField = {}, nullSetDefaultValue = true)
public class KeyListVO extends BaseVO {
	private List<Key> list;	// key
	private Page page;	// 分页
	
	public List<Key> getList() {
		return this.list;
	}
	public void setList(List<Key> list) {
		this.list = list;
	}
	public Page getPage() {
		return page;
	}
	public void setPage(Page page) {
		this.page = page;
	}
	
	@Override
	public String toString() {
		return "KeyListVO [list=" + list + ", page=" + page + "]";
	}
}
