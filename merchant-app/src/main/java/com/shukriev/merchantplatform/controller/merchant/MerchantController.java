package com.shukriev.merchantplatform.controller.merchant;


import com.shukriev.merchantplatform.exception.InvalidParameterException;
import com.shukriev.merchantplatform.inbound.merchant.MerchantService;
import com.shukriev.merchantplatform.model.merchant.NormalMerchant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(value = "/merchants")
public class MerchantController {
	private final MerchantService merchantService;

	@Autowired
	public MerchantController(MerchantService merchantService) {
		this.merchantService = merchantService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<NormalMerchant>> getMerchants() {
		final var merchants = merchantService.getMerchants();
		return new ResponseEntity<>(merchants, HttpStatus.OK);
	}

	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getMerchant(@PathVariable String id) {
		if (StringUtils.isEmpty(id) || "null".equals(id)) {
			throw new InvalidParameterException("[id] parameter is mandatory");
		}

		final var merchants = merchantService.getById(UUID.fromString(id));
		return new ResponseEntity<>(merchants, HttpStatus.OK);
	}

	@PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> editMerchant(@PathVariable String id,
										  @RequestBody final NormalMerchant merchant) {
		if (StringUtils.isEmpty(id) || !id.equals(merchant.getId().toString())) {
			throw new InvalidParameterException("The id parameter is mandatory and should be matching to payload.id");
		}

		return new ResponseEntity<>(merchantService.updateMerchant(merchant), HttpStatus.OK);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<?> deleteMerchant(@PathVariable String id) {
		if (StringUtils.isEmpty(id)) {
			throw new InvalidParameterException("[id] parameter is mandatory");
		}

		merchantService.deleteMerchant(UUID.fromString(id));
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
