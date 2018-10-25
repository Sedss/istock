package io.github.kingschan1204.istock.module.maindata.services;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import io.github.kingschan1204.istock.common.util.stock.StockSpider;
import io.github.kingschan1204.istock.module.maindata.po.Stock;
import io.github.kingschan1204.istock.module.maindata.po.StockCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

/**
 *
 * @author chenguoxiang
 * @create 2018-04-08 17:11
 **/
@Service
public class StockCodeService {

    private Logger log = LoggerFactory.getLogger(StockCodeService.class);
    @Autowired
    private StockSpider stockSpider;
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 保存所有代码
     *
     * @throws Exception
     */
    public void saveAllStockCode() throws Exception {
        List<String> szCodes = stockSpider.getStockCodeBySZ();
        List<String> shCodes = stockSpider.getStockCodeBySH();
        List<String> allCodes = stockSpider.getAllStockCode();
        HashSet<String> codes = new HashSet<String>();
        codes.addAll(szCodes);
        codes.addAll(shCodes);
        codes.addAll(allCodes);
        codes.stream().forEach(code -> {
                    mongoTemplate.upsert(
                            new Query(Criteria.where("_id").is(code)),
                            new Update().set("_id", code).set("hrdud",0).set("xlsError",0),
                            "stock_code"
                    );
                }
        );


    }


    /**
     * 返回所有代码
     *
     * @return
     */
    public List<StockCode> getAllStockCodes() {
        return mongoTemplate.find(new Query(), StockCode.class);

    }

    /**
     * 返回沪市代码
     * @return
     */
    public List<StockCode> getSHStockCodes() {
        return mongoTemplate.find(new Query(Criteria.where("_id").regex("sh")), StockCode.class);

    }

    /**
     * 返回深市代码
     * @return
     */
    public List<StockCode> getSZStockCodes() {
        return mongoTemplate.find(new Query(Criteria.where("_id").regex("sz")), StockCode.class);
    }



}
