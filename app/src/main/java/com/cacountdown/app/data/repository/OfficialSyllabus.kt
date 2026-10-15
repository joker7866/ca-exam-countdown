package com.cacountdown.app.data.repository

/**
 * The real ICAI Intermediate (New Scheme) chapter lists, one entry per
 * subjectCode as used in the bundled timetable JSON. Kept at chapter
 * granularity (not exploded into every sub-unit) so the topic checklist
 * stays a manageable size per subject. A subjectCode with no entry here
 * (a future attempt, or Foundation/Final) falls back to generic numbered
 * chapters in [ProgressRepository].
 */
object OfficialSyllabus {

    val CHAPTERS_BY_SUBJECT: Map<String, List<String>> = mapOf(
        // Paper 1: Advanced Accounting
        "INTER_P1" to listOf(
            "Introduction to Accounting Standards",
            "Framework for Preparation and Presentation of Financial Statements",
            "Applicability of Accounting Standards",
            "Presentation & Disclosures Based Accounting Standards",
            "Assets Based Accounting Standards",
            "Liabilities Based Accounting Standards",
            "Accounting Standards Based on Items Impacting Financial Statement",
            "Revenue Based Accounting Standards",
            "Other Accounting Standards",
            "Accounting Standards for Consolidated Financial Statements",
            "Financial Statements of Companies",
            "Buyback of Securities",
            "Amalgamation of Companies",
            "Internal Reconstruction",
            "Accounting for Branches including Foreign Branches",
        ),
        // Paper 2: Corporate & Other Laws
        "INTER_P2" to listOf(
            "Preliminary",
            "Incorporation of Company and Matters Incidental Thereto",
            "Prospectus and Allotment of Securities",
            "Share Capital and Debentures",
            "Acceptance of Deposits by Companies",
            "Registration of Charges",
            "Management & Administration",
            "Declaration and Payment of Dividend",
            "Accounts of Companies",
            "Audit and Auditors",
            "Companies Incorporated Outside India",
            "The Limited Liability Partnership Act, 2008",
            "Other Laws: The General Clauses Act, 1897",
            "Other Laws: Interpretation of Statutes",
            "Other Laws: The Foreign Exchange Management Act, 1999",
        ),
        // Paper 3: Taxation (Income-tax Law + GST)
        "INTER_P3" to listOf(
            "Income-tax: Basic Concepts",
            "Income-tax: Residence and Scope of Total Income",
            "Income-tax: Heads of Income",
            "Income-tax: Income of Other Persons included in Assessee's Total Income",
            "Income-tax: Aggregation of Income, Set-Off and Carry Forward of Losses",
            "Income-tax: Deductions from Gross Total Income",
            "Income-tax: Advance Tax, TDS and TCS",
            "Income-tax: Filing of Return of Income and Self Assessment",
            "Income-tax: Income Tax Liability - Computation and Optimisation",
            "GST: GST in India - An Introduction",
            "GST: Supply under GST",
            "GST: Charge of GST",
            "GST: Place of Supply",
            "GST: Exemptions from GST",
            "GST: Time of Supply",
            "GST: Value of Supply",
            "GST: Input Tax Credit",
            "GST: Registration",
            "GST: Tax Invoice; Credit and Debit Notes",
            "GST: Accounts and Records",
            "GST: E-Way Bill",
            "GST: Payment of Tax",
            "GST: Tax Deduction and Collection at Source",
        ),
        // Paper 4: Cost & Management Accounting
        "INTER_P4" to listOf(
            "Introduction to Cost and Management Accounting",
            "Material Cost",
            "Employee Cost and Direct Expenses",
            "Overheads – Absorption Costing Method",
            "Activity Based Costing",
            "Cost Sheet",
            "Cost Accounting Systems",
            "Unit & Batch Costing",
            "Job Costing",
            "Process & Operation Costing",
            "Joint Products and By Products",
            "Service Costing",
            "Standard Costing",
            "Marginal Costing",
            "Budgets and Budgetary Control",
        ),
        // Paper 5: Auditing & Ethics
        "INTER_P5" to listOf(
            "Nature, Objective and Scope of Audit",
            "Audit Strategy, Audit Planning and Audit Programme",
            "Risk Assessment and Internal Control",
            "Audit Evidence",
            "Audit of Items of Financial Statements",
            "Audit Documentation",
            "Completion and Review",
            "Audit Report",
            "Special Features of Audit of Different Type of Entities",
            "Audit of Banks",
            "Ethics and Terms of Audit Engagements",
        ),
        // Paper 6: Financial Management & Strategic Management
        "INTER_P6" to listOf(
            "FM: Scope and Objectives of Financial Management",
            "FM: Types of Financing",
            "FM: Financial Analysis and Planning – Ratio Analysis",
            "FM: Cost of Capital",
            "FM: Financing Decisions – Capital Structure",
            "FM: Financing Decisions – Leverages",
            "FM: Investment Decisions",
            "FM: Dividend Decision",
            "FM: Management of Working Capital",
            "SM: Introduction to Strategic Management",
            "SM: Strategic Analysis: External Environment",
            "SM: Strategic Analysis: Internal Environment",
            "SM: Strategic Choices",
            "SM: Strategy Implementation and Evaluation",
        ),
    )

    fun chaptersFor(subjectCode: String): List<String>? = CHAPTERS_BY_SUBJECT[subjectCode]
}
