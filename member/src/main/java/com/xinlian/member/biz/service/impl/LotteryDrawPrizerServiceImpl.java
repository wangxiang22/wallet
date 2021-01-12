package com.xinlian.member.biz.service.impl;

import com.xinlian.biz.model.LotteryDrawPrizer;
import com.xinlian.biz.dao.LotteryDrawPrizerMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xinlian.common.response.ResponseResult;
import com.xinlian.common.result.BizException;
import com.xinlian.member.biz.redis.RedisClient;
import com.xinlian.member.biz.service.LotteryDrawPrizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.xinlian.member.biz.redis.RedisConstant.DRAWER;

/**
 * <p>
 * 中奖者表 服务实现类
 * </p>
 *
 * @author lx
 * @since 2020-06-05
 */
@Service
public class LotteryDrawPrizerServiceImpl extends ServiceImpl<LotteryDrawPrizerMapper, LotteryDrawPrizer> implements LotteryDrawPrizerService {
    Boolean flag = true;
    @Autowired
    private LotteryDrawPrizerMapper lotteryDrawPrizerMapper;
    @Autowired
    private RedisClient redisClient;

    /**
     * 返回10个中奖者
     * 从123等奖随机抽取一个加入
     * 从4等奖随机抽取一个加入
     * 从任意奖抽取8个加入
     *
     * @return
     */
    @Override
    public ResponseResult runningHorseLight() {
        if (redisClient.get(DRAWER) == null) {
            List<LotteryDrawPrizer> prize123List = lotteryDrawPrizerMapper.queryPrize123();//最新123等奖随机10个
            List<LotteryDrawPrizer> prize4List = lotteryDrawPrizerMapper.queryPrize4();//最新四等奖10个
            List<LotteryDrawPrizer> random8List = lotteryDrawPrizerMapper.queryPrizeRandom8();//最新80个任意奖
            ArrayList<LotteryDrawPrizer> list = new ArrayList<>();
            Collections.shuffle(random8List);
            LotteryDrawPrizer prize123 = prize123List.get(new Random().nextInt(prize123List.size()));
            LotteryDrawPrizer prize4 = prize4List.get(new Random().nextInt(prize123List.size()));
            list.add(prize123);
            list.add(prize4);
            for (LotteryDrawPrizer lotteryDrawPrizer : random8List) {
                boolean present = list.stream().filter(l -> l.getId().equals(lotteryDrawPrizer.getId())).findAny().isPresent();
                if (!present) {
                    list.add(lotteryDrawPrizer);
                }
                if (list.size() > 9) {
                    break;
                }
            }
            redisClient.set(DRAWER,list);
            return ResponseResult.ok(list);
        } else {
            return ResponseResult.ok(redisClient.get(DRAWER));
        }
    }

    @Override
    public ResponseResult insert10() {
        if (!flag) {
            throw new BizException("搞来搞去了还");
        }
        String[] strArr = {"yyh11990", "nj2866633232", "13877764783", "吴大伟", "loi99384", "zfc999", "wq89912", "cvt90382", "pp999877", "13884685784", "fasd213123"};
        LinkedList<LotteryDrawPrizer> lotteryDrawPrizers = new LinkedList<>();
        Set set = new HashSet();
        while (true) {
            Long i = new Random().nextLong();
            set.add(i);
            if (set.size() > 10) {
                break;
            }
        }
        ArrayList<Long> arrayList = new ArrayList(set);
        for (int i = 0; i < 10; i++) {
            LotteryDrawPrizer lotteryDrawPrizer = new LotteryDrawPrizer();
            lotteryDrawPrizer.setUsername(strArr[i]);
            switch (i) {
                case 1:
                    lotteryDrawPrizer.setPrize("二等奖");
                    break;
                case 8:
                    lotteryDrawPrizer.setPrize("三等奖");
                    break;
                default:
                    lotteryDrawPrizer.setPrize("四等奖");
            }
            lotteryDrawPrizer.setCreateTime(new Date());
            lotteryDrawPrizer.setUid(System.currentTimeMillis() - arrayList.get(i));
            lotteryDrawPrizers.add(lotteryDrawPrizer);
        }
        ArrayList<LotteryDrawPrizer> list = redisClient.get(DRAWER);
        if (list==null){
            list = new ArrayList<>();
        }
        list.addAll(lotteryDrawPrizers);
        redisClient.set(DRAWER, list);
        insertBatch(lotteryDrawPrizers);
        flag = false;
        return ResponseResult.ok();
    }
}
