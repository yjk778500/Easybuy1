package com.web.pre;

import com.dao.EasybuyCollectMapper;
import com.dao.ProductCategoryMapper;
import com.dao.ProductMapper;
import com.entity.EasybuyCollect;
import com.entity.EasybuyNews;
import com.entity.EasybuyProduct;
import com.entity.EasybuyUser;
import com.entity.EasybuyProductCategory;
import com.service.order.impl.CartServiceImpl;
import com.service.product.impl.ProductCategoryServiceImpl;
import com.service.product.impl.ProductServiceImpl;
import com.utils.MyBatisUtil;
import com.utils.Pager;
import com.utils.ReturnResult;
import com.web.AbstractServlet;
import java.io.IOException;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 商品信息控制层！
 * Servlet implementation class ProductServlet
 */
@WebServlet("/ProductServlet")
public class ProductServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	private static final String TMP_DIR_PATH = "D:\\tmp";
	private File tmpDir;
	private static final String DESTINATION_DIR_PATH = "/files";
	private File destinationDir;
	
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		tmpDir = new File(TMP_DIR_PATH);
		if (!tmpDir.exists()) {//如果目录不存在，则新建目录
			tmpDir.mkdirs();
		}
		String realPath = getServletContext().getRealPath(DESTINATION_DIR_PATH);
		destinationDir = new File(realPath);
		destinationDir.mkdirs();
		if (!destinationDir.isDirectory()) {
			throw new ServletException(DESTINATION_DIR_PATH
					+ " is not a directory");
		}
		
	}
	/**
	 * Default constructor.
	 */
	public ProductServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	/**
	 * 查看商品列表  (后端)
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String index(HttpServletRequest request, HttpServletResponse response) throws Exception{
		int currentPage = 1; //默认第一页
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//获得所有商品条数
		int count = psi.getTotalCount();
		Pager pager = new Pager(count,10,currentPage);
		//设置pager URL
		pager.setUrl("/ProductServlet?action=index");
		//得到所有商品
		List<EasybuyProduct> list = psi.getAllProduct(pager);
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("productList", list);
		request.setAttribute("pager", pager);
		return "/backend/product/productList";
	}
	
	/**
	 * 修改商品信息！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updateAndDel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "/backend/product/productList";
	}
	
	/**
	 * @param request
	 * @return
	 */
	private List<EasybuyCollect> getUserFromSession(HttpServletRequest request) {
		return null;
	}

	/**
	 * 获取一级分类！(模糊查询)
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryProductList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int rowCount = 0; //总信息条数
		int category = 0;
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//是否根据分类查询商品信息
		//得到条数默认得到所有条数
		rowCount = psi.getProductRowCount(category);
		if(null != request.getParameter("category") && Integer.parseInt(request.getParameter("category"))!= 0){
			category = Integer.parseInt(request.getParameter("category"));
			//得到该分类所有商品条数
			rowCount = psi.getProductRowCount(category);
		}
		String proName = ""; //判断用户是否模糊查询
		if(null != request.getParameter("keyWord")){
			proName = request.getParameter("keyWord");
			//得到模糊查询条数
			rowCount = psi.getmohuCount(proName);
		}
		int currentPage = 1;
		//判断当前页是否为空
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		Pager pager = new Pager(rowCount,8,currentPage);
		//判断用户是否登录/登录加载用户收藏
		if(null != request.getParameter("userId") && "" != request.getParameter("userId")){
			int userId = Integer.parseInt(request.getParameter("userId"));
			SqlSession ss2 = MyBatisUtil.getSqlSession();
			CartServiceImpl csi = new CartServiceImpl(ss2.getMapper(EasybuyCollectMapper.class));
			List<EasybuyCollect> collectList = csi.selectByUserId(userId);
			MyBatisUtil.closeSqlSession(ss2);
			//存储到request中
			request.setAttribute("listCollect", collectList);
			pager.setUrl("ProductServlet?action=queryProductList&category="+category+"&userId="+userId);
		}else{
			pager.setUrl("ProductServlet?action=queryProductList&category="+category);
		}
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		//得到所有商品
		List<EasybuyProduct> list = psi.getEasybuyProductListByCategoryId(category, pager,proName);

		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("productList", list);
		request.setAttribute("pager", pager);
		//存储用户需要搜索的值
		request.setAttribute("keyWord", proName);
		//存储商品格式
		request.setAttribute("total", rowCount);
		
		return "/pre/product/queryProductList";
	}
	
	public String queryLikeProductList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "/pre/product/queryProductList";
	}
	

	/**
	 * 获取二级分类！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryProductList2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int category = Integer.parseInt(request.getParameter("category"));
		int currentPage = 1;
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//判断当前页是否为空
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		Pager pager = new Pager(psi.getProductRowCount(category),8,currentPage);
		pager.setUrl("ProductServlet?action=queryProductList2&category="+category);
		//获得二级分类商品
		List<EasybuyProduct> proList = psi.getEasybuyProductListByCategoryId2(category, pager);
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		//判断用户是否登录
		if(null != request.getSession().getAttribute("easybuyUserLogin")){  //已登录 加载用户收藏
			int userId = ((EasybuyUser)request.getSession().getAttribute("easybuyUserLogin")).getId();
			SqlSession ss2 = MyBatisUtil.getSqlSession();
			CartServiceImpl csi = new CartServiceImpl(ss2.getMapper(EasybuyCollectMapper.class));
			//加载收藏
			List<EasybuyCollect> list = csi.selectByUserId(userId);
			MyBatisUtil.closeSqlSession(ss2);
			//存储到request中
			request.setAttribute("listCollect", list);
		}
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("productList", proList);
		request.setAttribute("pager", pager);
		return "/pre/product/queryProductList";
	}

	/**
	 * 获取三级分类！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryProductList3(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int category = Integer.parseInt(request.getParameter("category"));  //三级分类id
		int currentPage = 1;
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//判断当前页是否为空
		if(null != request.getParameter("currentPage")){
			currentPage = Integer.parseInt(request.getParameter("currentPage"));
		}
		Pager pager = new Pager(psi.getProductRowCount(category),4,currentPage);
		pager.setUrl("ProductServlet?action=queryProductList3&category="+category);
		//得到三级分类商品
		List<EasybuyProduct> proList = psi.getEasybuyProductListByCategoryId3(category, pager);
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		//判断用户是否登录
		if(null != request.getSession().getAttribute("easybuyUserLogin")){  //已登录 加载用户收藏
			int userId = ((EasybuyUser)request.getSession().getAttribute("easybuyUserLogin")).getId();
			SqlSession ss2 = MyBatisUtil.getSqlSession();
			CartServiceImpl csi = new CartServiceImpl(ss2.getMapper(EasybuyCollectMapper.class));
			//加载收藏
			List<EasybuyCollect> list = csi.selectByUserId(userId);
			MyBatisUtil.closeSqlSession(ss2);
			//存储到request中
			request.setAttribute("listCollect", list);
		}
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("productList", proList);
		request.setAttribute("pager", pager);
		return "/pre/product/queryProductList";
	}
	
	
	/**
	 * 获得收藏夹
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String queryProductList4(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = Integer.parseInt(request.getParameter("userId"));
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		CartServiceImpl csi = new CartServiceImpl(ss.getMapper(EasybuyCollectMapper.class));
		List<EasybuyCollect> list = csi.selectByUserId(userId);  //获得该用户所有收藏
		List<EasybuyProduct> proList = new ArrayList<>();
		EasybuyProduct ebp = null;
		EasybuyCollect ec = null;
		//得到用户收藏的商品信息
		for(int i=0;i<list.size();i++){
			ebp = new EasybuyProduct();
			ec = list.get(i);
			ebp = psi.findById(ec.getProductId());
			proList.add(ebp);
		}

		//加载分类
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		MyBatisUtil.closeSqlSession(ss);
		//存储到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("productList", proList);
		request.setAttribute("listCollect", list);
		request.setAttribute("total", proList.size());
		
		return "/pre/product/queryProductList";
	}

	/**
	 * 获得热门商品详细信息
	 * @param request
	 * @param response
	 * @return
	 */
	public String queryProductDeatil(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("id")); //商品编号
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		EasybuyProduct ebp = psi.findById(id); //得到商品对象
		if("" != request.getParameter("userId") && null != request.getParameter("userId")){ //如果不是空的则是登录状态 加载用户的收藏夹
			int userId = Integer.parseInt(request.getParameter("userId")); //该用户id
			SqlSession ss2 = MyBatisUtil.getSqlSession();
			CartServiceImpl csi = new CartServiceImpl(ss2.getMapper(EasybuyCollectMapper.class));
			List<EasybuyCollect> list = csi.selectByUserId(userId); //获得该用户所有的收藏
			MyBatisUtil.closeSqlSession(ss2);
			request.setAttribute("easybuyCollect", list);
		}
		//加载分类
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		List<EasybuyProductCategory> list1 = pcsi.getProductCategoryListOne();
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListTwo();
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListThree();
		MyBatisUtil.closeSqlSession(ss);
		//存到request作用域中
		request.setAttribute("list", list1);
		request.setAttribute("list2", list2);
		request.setAttribute("list3", list3);
		request.setAttribute("product", ebp);
		
		return "/pre/product/productDeatil";
	}

	/**
	 * 根据id删除商品！
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult deleteById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int productId = Integer.parseInt(request.getParameter("id")); //商品id
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		int count = psi.delEasybuyProductById(productId);
		ss.commit();
		MyBatisUtil.closeSqlSession(ss);
		ReturnResult result = new ReturnResult();
		if(count > 0){ //删除成功!
			return result.returnSuccess();
		}else{
			return result.returnFail("删除失败!");
		}
	}

	/**
	 * 修改或者上架操作！
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ReturnResult tomodify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return null;
	}

	/**
	 * 商品上架！
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public String toAddUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//加载第一级商品
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		List<EasybuyProductCategory> list = pcsi.getProductCategoryListOne();
		MyBatisUtil.closeSqlSession(ss);
		request.setAttribute("listOne", list);
		
		return "/backend/product/toAddProduct";
	}

	/**
	 * 根据Id查询对应的商品信息传递到修改页面！(回显数据)
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String getProduct(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));  //编号
		int currentPage =  Integer.parseInt(request.getParameter("currentPage")); //在哪一页修改
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		ProductCategoryServiceImpl pcsi = new ProductCategoryServiceImpl(ss.getMapper(ProductCategoryMapper.class));
		EasybuyProduct pro = psi.getEasybuyProductById(id);
		//加载第一级商品
		List<EasybuyProductCategory> list = pcsi.getProductCategoryListOne();
		//加载第一级商品分类下的二级分类
		List<EasybuyProductCategory> list2 = pcsi.getProductCategoryListByparentId(2, pro.getCategoryLevel1());
		//加载第二季商品分类下的三级分类
		List<EasybuyProductCategory> list3 = pcsi.getProductCategoryListByparentId(3, pro.getCategoryLevel2());
		//得到该商品信息
		
		MyBatisUtil.closeSqlSession(ss);
		//存储到request中
		request.setAttribute("listOne", list);
		request.setAttribute("listTwo", list2);
		request.setAttribute("listThree", list3);
		request.setAttribute("easybuyProduct", pro);
		request.setAttribute("currentPage", currentPage);
		return "/backend/product/toAddProduct";
	}

	/**
	 * 商品三级分类！！
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public void getCategoryList(HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	}

/**
 * 上传
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
	public ReturnResult getImgs(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String realPath = request.getSession().getServletContext().getRealPath("/files"); //上传路径
		String fileUploadName = "";  //文件名
		String fileName = ""; //字段名
		String id = ""; //判断进行添加还是修改
		EasybuyProduct pro = new EasybuyProduct();//商品类对象
		boolean ismultiPart = ServletFileUpload.isMultipartContent(request); //判断是否为multiPart类型
		if(ismultiPart){
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			Iterator<FileItem> ito = items.iterator();
			while(ito.hasNext()){
				FileItem item = ito.next();
				if(item.isFormField()){  //普通字段
					fileName = item.getFieldName();
					if(fileName.equals("categoryLevel1Id")){
						pro.setCategoryLevel1(Integer.parseInt(item.getString("UTF-8")));
					}else if(fileName.equals("categoryLevel2Id")){
						pro.setCategoryLevel2(Integer.parseInt(item.getString("UTF-8")));
					}else if(fileName.equals("categoryLevel3Id")){
						pro.setCategoryLevel3(Integer.parseInt(item.getString("UTF-8")));
					}else if(fileName.equals("name")){
						pro.setName(item.getString("UTF-8"));
					}else if(fileName.equals("price")){
						pro.setPrice(Float.valueOf(item.getString("UTF-8")));
					}else if(fileName.equals("stock")){
						pro.setStock(Integer.parseInt(item.getString("UTF-8")));
					}else if(fileName.equals("description")){
						pro.setDescription(item.getString("UTF-8"));
					}else if(fileName.equals("id")){
						id = item.getString("UTF-8");
					}else if(fileName.equals("fileName")){
						pro.setFileName(item.getString("UTF-8"));
					}
				}else{  //文件类型
					fileUploadName = item.getName();
					System.out.println(fileUploadName);
					//文件格式限制
					List<String> list = Arrays.asList("jpg");
					String ex = fileUploadName.substring(fileUploadName.indexOf(".")+1);
					if(!list.contains(ex)){
						System.out.println("文件格式错误!");
					}else{ //允许上传
						File saveFile = new File(realPath,fileUploadName);
						pro.setFileName(fileUploadName); //存储文件名
						item.write(saveFile);
						//System.out.println("上传成功!文件名为:"+item.getName());
					}
				}
			}
		}
		SqlSession ss = MyBatisUtil.getSqlSession();
		ProductServiceImpl psi = new ProductServiceImpl(ss.getMapper(ProductMapper.class));
		//添加商品
		ReturnResult result = new ReturnResult();
		//判断是添加还是修改
		if(id.equals("")){  //添加
			pro.setIsDelete(0);
			int count = psi.addEasybuyProduct(pro);
			ss.commit();
			MyBatisUtil.closeSqlSession(ss);
			if(count > 0){  //成功
				return result.returnSuccess("添加成功!");
			}else{//失败
				return result.returnFail("添加失败!");
			}
		}else{ //修改
			pro.setId(Integer.parseInt(id));
			pro.setIsDelete(0);
			int count = psi.updateEasybuyProduct(pro);
			ss.commit();
			MyBatisUtil.closeSqlSession(ss);
			if(count > 0){
				return result.returnSuccess("修改成功!");
			}else{
				return result.returnFail("修改失败!");
			}
		}
	}

	@Override
	public Class getServletClass() {
		return ProductServlet.class;
	}

}
