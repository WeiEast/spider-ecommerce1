package com.datatrees.rawdatacentral.evidence.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.datatrees.rawdatacentral.evidence.common.DBHelper;
import com.datatrees.rawdatacentral.evidence.dto.ObjectResult;
import com.datatrees.rawdatacentral.evidence.oss.OssService;
import com.datatrees.rawdatacentral.evidence.oss.OssServiceProvider;

import net.sf.json.JSONSerializer;

@Controller
@EnableAutoConfiguration
public class EvidenceController {

	@RequestMapping(value = "/getDetails/{userId}/{first}/{second}/{third}/{pageNum}/{pageSize}", method = RequestMethod.GET)
	@ResponseBody
	public Object getDetails(@PathVariable String userId, @PathVariable String first, @PathVariable String second,
			@PathVariable String third,@PathVariable String pageSize,@PathVariable String pageNum) {
		StringBuffer sql = new StringBuffer("");
		String sqlTemplate = "select a.id,a.userID,a.taskId,a.resultType,a.storagePath,a.remark,b.websiteName,b.websiteType,a.status,a.createdAt,a.url "
				+ "from {0} as a inner join " + "t_website as b on a.websiteID = b.id and a.userId =" + userId;
				//+ " and a.CreatedAt in (select max(x.CreatedAt) from {0} as x where x.UniqueMd5 = a.UniqueMd5)";
		// 运营商
		if (first.equals("1")) {
			sql.append(sqlTemplate.replace("{0}", "t_operator_extract_result"));
		}
		// 电商
		if (first.equals("2")) {
			sql.append(sqlTemplate.replace("{0}", "t_ecommerce_extract_result"));
		}
		// 邮箱
		if (first.equals("3")) {
			sql.append(
					"select a.id,a.userId,a.taskId,a.resultType,a.storagePath,a.remark,b.websiteName,b.websiteType,a.status,a.createdAt,a.url"
							+ ",a.sender,a.subject,a.receiveAt"
							+ " from t_mail_extract_result as a inner join t_website as b"
							+ " on a.websiteId =b.id and a.userId=" + userId);
							//+ " and a.CreatedAt in (select max(x.CreatedAt) from t_mail_extract_result as x where x.UniqueMd5 = a.UniqueMd5)");
			if (!third.equals("-1")) {
				sql.append(" and a.bankId=" + third);
			}
		}
		// 默认信息
		if (first.equals("4")) {
			sql.append(sqlTemplate.replace("{0}", "t_default_extract_result"));
		}
		// 网上银行
		if (first.equals("5")) {
			sql.append(sqlTemplate.replace("{0}", "t_ebank_extract_result"));
		}
		// 信用卡账单
		if (first.equals("6")) {
			sql.append(
					"select a.id,a.userId,a.taskId,a.resultType,a.storagePath,a.remark,b.websiteName,b.websiteType,a.status,a.createdAt,a.url"
							+ ",a.sender,a.subject,a.receiveAt"
							+ " from t_mail_extract_result as a inner join t_website as b"
							+ " on a.websiteId =b.id and a.userId=" + userId);
							//+ " and a.CreatedAt in (select max(x.CreatedAt) from t_mail_extract_result as x where x.UniqueMd5 = a.UniqueMd5)");
			if (!second.equals("0")) {
				sql.append(" and a.bankId=" + second);
			}
		}
		if (!second.equals("0") && !first.equals("6")) {
			sql.append(" and a.websiteId=" + second);
		}
		sql.append(" order by a.taskId desc limit "+Integer.parseInt(pageNum)*Integer.parseInt(pageSize)+", "+pageSize);

		DBHelper db = new DBHelper(sql.toString());
		try {
			ResultSet ret = db.pst.executeQuery();
			List<ObjectResult> resultList = new ArrayList<ObjectResult>();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			while (ret.next()) {
				ObjectResult temp = new ObjectResult();
				temp.setUserId(ret.getInt(2));
				temp.setTaskId(ret.getInt(3));
				temp.setResultType(ret.getString(4));
				temp.setStoragePath(ret.getString(5));
				temp.setRemark(ret.getString(6));
				temp.setWebsiteName(ret.getString(7));
				temp.setWebsiteType(ret.getString(8));
				temp.setStatus(ret.getInt(9));
				temp.setCreatedAt(formatter.format(ret.getTimestamp(10)));
				if(ret.getString(11)!=null){
					temp.setUrl(ret.getString(11));
				}
				if (first.equals("3") || first.equals("6")) {
					if(ret.getString(12)!=null){
						temp.setSender(ret.getString(12));
					}
					if(ret.getString(13)!=null){
						temp.setSubject(ret.getString(13));
					}
					if(ret.getTimestamp(14)!=null){
						temp.setReceivedAt(formatter.format(ret.getTimestamp(14)));
					}
				}
				temp.setFlagId(Integer.valueOf(first));
				resultList.add(temp);
			}
			if (resultList != null && resultList.size() > 0)
				return JSONSerializer.toJSON(resultList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "No results";
	}

	// @RequestParam("storeagePath")
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public void getFile(@RequestParam("storeagePath") String storeagePath, HttpServletResponse response) {
		OssService service = OssServiceProvider.getDefaultService();
		byte[] returnObject = service.getObjectContent(storeagePath);
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			response.reset();// 设置为没有缓存
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "inline;fileName=" +storeagePath+ ".zip");
			response.setContentType("application/octet-stream");
			response.getOutputStream().write(returnObject);
			response.getOutputStream().flush();
			response.getOutputStream().close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/select/{selectId}", method = RequestMethod.GET)
	@ResponseBody
	public Object selectChange(@PathVariable String selectId) {
		StringBuffer sql = new StringBuffer("");
		if (selectId.equals("1")) {
			sql.append("select a.WebsiteId,a.OperatorName from t_operator as a");
		}
		if (selectId.equals("2")) {
			sql.append("select a.WebsiteId,a.EcommerceName from t_ecommerce as a");
		}
		if (selectId.equals("3")) {
			sql.append("select a.id,a.WebsiteName from t_website as a where a.WebsiteType=\"mail\"");
		}
		if (selectId.equals("4")) {
			sql.append("select a.id,a.WebsiteName from t_website as a where a.WebsiteType=\"mail\"");
		}
		if (selectId.equals("5")) {
			sql.append("select a.id,a.WebsiteName from t_website as a where a.WebsiteType=\"bank\"");
		}
		if (selectId.equals("6")) {
			sql.append("select a.id,a.bankName from t_bank as a");
		}

		if (sql.toString().equals("")) {
			return "No results";
		}
		DBHelper db = new DBHelper(sql.toString());
		try {
			ResultSet ret = db.pst.executeQuery();
			List<ObjectResult> resultList = new ArrayList<ObjectResult>();
			while (ret.next()) {
				ObjectResult temp = new ObjectResult();
				temp.setWebsiteId(ret.getInt(1));
				temp.setWebsiteName(ret.getString(2));
				resultList.add(temp);
			}
			if (resultList != null && resultList.size() > 0)
				return JSONSerializer.toJSON(resultList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Success";
	}
	
	public static void main(String[] args) {
		SpringApplication.run(EvidenceController.class, args);
	}
}
