# README
## Postman
### 新增交易
*  TradeDate 非必要
*  BranchNo 必要
*  CustSeq 必要
*  Stock 必要
*  price 必要且不得<10
*  Qty 必要且不得<1
> 若無 TradeDate 則自動存取為現在日期
### 未實現損益
*  BranchNo 必要
*  CustSeq 必要
*  Stock 非必要，若無則列出所有股票
*  minLimit 非必要
*  maxLimit 非必要
> 若只有 minLimit 則列出大於 minLimit 的所有股票 
> 若只有 maxLimit 則列出小於 maxLimit 的所有股票
> 若無區間條件 則列出所有股票

### 更新股價
*  Stock 必要
*  price 非必要
> 若無 price 則依據 RefPrice 隨機波動價格 且漲幅為 +- 10%
> 若有 price 且 ">10" 則依據 price 設定現價

### 查詢股票
*  Stock 必要


### 當日交割金查詢
*  BranchNo 必要
*  CustSeq 必要
> 列出明細與總金額
> 若今日為假日 則顯示 " 非交易時間 "

