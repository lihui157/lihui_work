package com.jhgzs.mobsite.obj;
/**
 * �������
 * �����ڷ�װhttp����������������ص���json��ʽ
 * @author lihui
 *
 */
public class WFM_Result {
		
		public WFM_Result(String resultTag,String resultMsg){
			this.resultTag = resultTag;
			this.resultMsg = resultMsg;
		}
		
		public WFM_Result(){
			
		};
		
		public static final String SUCCESS = "success";
		
		public static final String FAIL = "fail";
	
		private String resultTag;
		
		private String resultMsg;
		
		private Object resultData;

		public String getResultTag() {
			return resultTag;
		}

		public void setResultTag(String resultTag) {
			this.resultTag = resultTag;
		}

		public String getResultMsg() {
			return resultMsg;
		}

		public void setResultMsg(String resultMsg) {
			this.resultMsg = resultMsg;
		}

		public Object getResultData() {
			return resultData;
		}

		public void setResultData(Object resultData) {
			this.resultData = resultData;
		}
		
		
}
