package com.rsetiapp.core.uidai.crypto;


import javax.inject.Singleton;

import dagger.Component;
import kotlin.jvm.JvmDefaultWithCompatibility;

@Singleton
@Component(modules = {CryptLibModule.class})
public interface CryptoStrategy {

    String encrypt(String body) throws Exception;

    String decrypt(String data) throws Exception;
}


