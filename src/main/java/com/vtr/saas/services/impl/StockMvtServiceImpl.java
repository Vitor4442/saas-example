package com.vtr.saas.services.impl;


import com.vtr.saas.common.PageResponse;
import com.vtr.saas.entities.Product;
import com.vtr.saas.entities.StockMvt;
import com.vtr.saas.mappers.StockMvtMapper;
import com.vtr.saas.repositories.ProductRepository;
import com.vtr.saas.repositories.StockMvtRepository;
import com.vtr.saas.requests.StockMvtRequest;
import com.vtr.saas.responses.StockMvtResponse;
import com.vtr.saas.services.StockMvtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMvtServiceImpl implements StockMvtService {

    private final StockMvtRepository stockMvtRepository;
    private final ProductRepository productRepository;
    private final StockMvtMapper stockMvtMapper;

    @Override
    public void create(StockMvtRequest request) {
        checkIfProductExistsById(request.getProductId());
        final StockMvt entity = this.stockMvtMapper.toEntity(request);
        this.stockMvtRepository.save(entity);
    }

    @Override
    public void update(String id, StockMvtRequest request) {
        final Optional<StockMvt> stockMvt = this.stockMvtRepository.findById(id);
        if (stockMvt.isEmpty()) {
            log.debug("StockMvt does not exist");
            throw new EntityNotFoundException("StockMvt does not exist");
        }

        checkIfProductExistsById(request.getProductId());

        final StockMvt stockMvtToUpdate = this.stockMvtMapper.toEntity(request);
        stockMvtToUpdate.setId(id);
        this.stockMvtRepository.save(stockMvtToUpdate);
    }

    @Override
    public PageResponse<StockMvtResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvt = this.stockMvtRepository.findAll(pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvt.map(this.stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    @Override
    public StockMvtResponse findById(String id) {
        return this.stockMvtRepository.findById(id)
                .map(this.stockMvtMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));
    }

    @Override
    public void delete(String id) {
        final StockMvt stockMvt = this.stockMvtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));
        this.stockMvtRepository.delete(stockMvt);
    }

    @Override
    public PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAllByProductId(productId, pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvts.map(this.stockMvtMapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }

    private void checkIfProductExistsById (final String productId){
        final Optional<Product> product = this.productRepository.findById(productId);
        if (product.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }
    }


}
