package xwc.xwcjava.test.crypto;

import xwc.xwcjava.address.Address;
import xwc.xwcjava.address.AddressVersion;
import xwc.xwcjava.address.PrivateKeyGenerator;
import xwc.xwcjava.builder.TransactionBuilder;
import xwc.xwcjava.client.NodeClient;
import xwc.xwcjava.config.Constants;
import xwc.xwcjava.exceptions.CryptoException;
import xwc.xwcjava.exceptions.TransactionException;
import xwc.xwcjava.operation.NodeException;
import xwc.xwcjava.transaction.Transaction;
import xwc.xwcjava.utils.Numeric;
import xwc.xwcjava.utils.PrivateKeyUtil;
import org.bitcoinj.core.ECKey;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class SignatureTests {
    private static final Logger log = LoggerFactory.getLogger(SignatureTests.class);

    @Test
    public void testGeneratePrivateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        String privateKeyHex = Numeric.toHexStringNoPrefix(keyPair.getPrivate().getEncoded());
        log.info("generated privateKeyHex: {}", privateKeyHex);
    }

    private String getRefInfo() throws NodeException {
        String nodeRpcEndpoint = Constants.defaultWsRpcEndpoint;
        NodeClient nodeClient = new NodeClient(nodeRpcEndpoint);
        nodeClient.open();
        nodeClient.sendLogin();
        return nodeClient.getRefInfo();
    }

    @Test
    public void testContractTransfer() throws TransactionException, NodeException {
        String refInfo = getRefInfo();
        String chainId = Constants.mainnetChainId;
        String wifStr = "5KatTfFZseaDsDf1d3JHCdBcUDdJZCLS2RwbEgoix1zyUMuHqVr";
        String callerAddr = "XWCNbEG9tTn9zKrRAwRZ1Q6RtJjkkiBZEPoWa";
        String callerPubKey = "XWC8R3MLmhUuAwBy2fX4KX6ZACmGp6ZgurX2qSv1Pt88aQsmBhVLA";
        String contractId = "XWCCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
        BigDecimal transferAmount = new BigDecimal("0.001");
        String transferMemo = "hi";

        long gasLimit = 10000;
        long gasPrice = 1;

        BigDecimal fee = new BigDecimal("0.003");

        Transaction tx = TransactionBuilder.createContractTransferTransaction(refInfo, callerAddr, callerPubKey,
                contractId, transferAmount, Constants.XWC_ASSET_ID, Constants.xwcPrecision, transferMemo, fee, gasLimit, gasPrice, null);
        String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
        log.info("signed tx: {}", txJson);
    }

    @Test
    public void testContractInvoke() throws TransactionException, NodeException {
        String refInfo = getRefInfo();
        String chainId = Constants.mainnetChainId;
        String wifStr = "5KatTfFZseaDsDf1d3JHCdBcUDdJZCLS2RwbEgoix1zyUMuHqVr";
        String callerAddr = "XWCNbEG9tTn9zKrRAwRZ1Q6RtJjkkiBZEPoWa";
        String callerPubKey = "XWC8R3MLmhUuAwBy2fX4KX6ZACmGp6ZgurX2qSv1Pt88aQsmBhVLA";
        String contractId = "XWCCHcRE3jsyHGrtoE2ZJZtpHsEiYTQ7VrHkb";
        String contractApi = "balanceOf";
        String contractArg = "";

        long gasLimit = 10000;
        long gasPrice = 1;

        BigDecimal fee = new BigDecimal("0.003");

        Transaction tx = TransactionBuilder.createContractInvokeTransaction(refInfo, callerAddr, callerPubKey,
                contractId, contractApi, contractArg, fee, gasLimit, gasPrice, null);
        String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
        log.info("signed tx: {}", txJson);
    }

    @Test
    public void testTransfer() throws TransactionException, NodeException {
        String refInfo = getRefInfo();
        String chainId = Constants.mainnetChainId;
        String wifStr = "5KatTfFZseaDsDf1d3JHCdBcUDdJZCLS2RwbEgoix1zyUMuHqVr";
        String fromAddr = "XWCNbEG9tTn9zKrRAwRZ1Q6RtJjkkiBZEPoWa";
        String toAddr = "XWCNbEG9tTn9zKrRAwRZ1Q6RtJjkkiBZEPoWa";
        BigDecimal amount = new BigDecimal("0.001");
        BigDecimal fee = new BigDecimal("0.0011");
        String memo = "test";
        Transaction tx = TransactionBuilder.createTransferTransaction(refInfo, fromAddr, toAddr, amount, Constants.XWC_ASSET_ID, 5, fee, memo, null);
        String txJson = TransactionBuilder.signTransaction(tx, wifStr, chainId, Address.ADDRESS_PREFIX);
        log.info("signed tx: {}", txJson);
    }

    @Test
    public void testGeneratePrivateKeyAndAddress() throws CryptoException {
        ECKey ecKey = PrivateKeyGenerator.generate();
        String privateKeyHex = ecKey.getPrivateKeyAsHex();
        String wif = PrivateKeyUtil.privateKeyToWif(ecKey);
        log.info("privateKeyHex: {}", privateKeyHex);
        log.info("privateKey wif: {}", wif);
        Address address = Address.fromPubKey(ecKey.getPubKey(), AddressVersion.NORMAL);
        log.info("address: {}", address);
    }
}
