package com.vtr.saas.services;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.requests.StockMvtRequest;
import com.vtr.saas.responses.StockMvtResponse;

public interface StockMvtService extends BasicService <StockMvtRequest, StockMvtResponse> {
    PageResponse<StockMvtResponse> findAllByProductId(String productId, int page, int size);
}
