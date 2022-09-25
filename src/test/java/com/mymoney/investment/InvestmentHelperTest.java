package com.mymoney.investment;

import com.mymoney.investment.dao.DataStub;
import com.mymoney.investment.enums.Assets;
import com.mymoney.investment.service.InvestmentService;
import com.mymoney.investment.service.InvestmentServiceImpl;
import com.mymoney.investment.utils.InvestmentHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InvestmentHelperTest {

    @Mock
    private DataStub dataStub;
    @Mock private InvestmentService investmentService;
    @Spy
    private InvestmentHelper investmentHelper;

    @BeforeEach
    public void setUp() {
        dataStub = new DataStub();
        dataStub.defaultAssetOrderForIO.add(Assets.EQUITY);
        dataStub.defaultAssetOrderForIO.add(Assets.DEBT);
        dataStub.defaultAssetOrderForIO.add(Assets.GOLD);
        investmentService = new InvestmentServiceImpl(dataStub);
        investmentHelper = new InvestmentHelper(investmentService);
    }

    @Test
    void testWithInvalidFileInput() {
        assertThrows(IOException.class,
                () -> investmentHelper.processInvestment("inputFile"),
                "Expected Allocate method to throw Exception, but it didn't.");
    }

    @Test
    void testExecuteCommandsFromFileWithValidFile() throws IOException {
        String inputFile =
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("testInputFile"))
                        .getFile();
        String outputFile =
                Objects.requireNonNull(this.getClass().getClassLoader().getResource("testOutputFile"))
                        .getFile();
        List<String> output = investmentHelper.processInvestment(inputFile);
        try (Stream<String> lines = Files.lines(Paths.get(outputFile))) {
            String expectedResult = lines.map(String::trim).collect(Collectors.joining(";"));
            String result =
                    output.stream()
                            .filter(Objects::nonNull)
                            .map(String::trim)
                            .collect(Collectors.joining(";"));
            assertEquals(expectedResult, result);
        }
    }
}
