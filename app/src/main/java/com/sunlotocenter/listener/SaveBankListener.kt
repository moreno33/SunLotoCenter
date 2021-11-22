package com.sunlotocenter.listener

import com.sunlotocenter.dao.Bank

interface SaveBankListener {
    fun save(bank: Bank)
}