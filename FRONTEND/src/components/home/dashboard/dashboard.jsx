import React, { useState, useEffect } from "react";
import useQuery from "@/hooks/useQuery";
import {
  API_FORMS,
  DASHBOARD_ANALYTICS,
  DASHBOARD_RESPONSES,
} from "@/imports/api";
import { ResponseTable } from "./components/ResponseTable";
import { ChartAnalysis } from "./components/ChartAnalysis";
import { WordCloud } from "./components/WordCloud";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import DownloadCSV from "./components/downloadCSV";

function Dashboard() {
  const [formId, setFormId] = useState(null);

  // Fetch all forms
  const { data: formsData, loading } = useQuery(API_FORMS);

  // Fetch analytics for selected form
  const { data: analyticsData } = useQuery(
    formId ? `${DASHBOARD_ANALYTICS}/${formId}` : null,
    !formId
  );
  const { data: tableData } = useQuery(
    formId ? `${DASHBOARD_RESPONSES}/${formId}/user-responses` : null,
    !formId
  );

  useEffect(() => {
    if (formsData) {
      const validForm = formsData?.data?.find((f) => f.responseCount);
      if (validForm) setFormId(validForm?.id);
    }
  }, [formsData]);
  const filteredResponses = tableData?.data?.data ?? [];
  const today = new Date().toISOString().split("T")[0];
  const todayResponse = filteredResponses?.filter((item) =>
    item.submittedAt.startsWith(today)
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-lg text-muted-foreground">Loading...</div>
      </div>
    );
  }

  return (
    <div className="container mx-auto p-6 space-y-6">
      <div className="flex flex-col space-y-4">
        <h1 className="text-3xl font-bold tracking-tight">Response Analysis</h1>

        {/* Form Selector */}
        <div className="flex items-center space-x-4">
          <select
            className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
            value={formId || ""}
            onChange={(e) => setFormId(e.target.value)}
          >
            <option value="">Select a form</option>
            {formsData?.data?.length ? (
              formsData?.data?.map((form) => (
                <option
                  disabled={!form?.responseCount}
                  key={form.uid}
                  value={form.id}
                >
                  {form.title}
                </option>
              ))
            ) : (
              <></>
            )}
          </select>
        </div>
      </div>

      {formId ? (
        <div className="grid gap-6">
          {/* Summary Cards */}
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Total Responses</CardTitle>
                <CardDescription className="text-2xl font-bold">
                  {filteredResponses?.length || 0}
                </CardDescription>
              </CardHeader>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>Today's Responses</CardTitle>
                <CardDescription className="text-2xl font-bold">
                  {todayResponse?.length || 0}
                </CardDescription>
              </CardHeader>
            </Card>
          </div>

          {/* Detailed Analysis */}
          <Card>
            <CardHeader>
              <CardTitle>Response Analysis</CardTitle>
              <CardDescription>
                Showing {filteredResponses.length} responses
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Tabs defaultValue="charts" className="space-y-4">
                <div className="flex justify-between">
                  <TabsList>
                    <TabsTrigger value="charts">Charts</TabsTrigger>
                    <TabsTrigger value="table">Table View</TabsTrigger>
                  </TabsList>

                  <div>
                    <DownloadCSV formId={formId} />
                  </div>
                </div>

                <TabsContent value="table" className="space-y-4">
                  <ResponseTable responses={filteredResponses} />
                </TabsContent>

                <TabsContent value="charts" className="space-y-4">
                  {/* Categorical Data Charts */}
                  {analyticsData?.data?.data?.categoricalSummary && (
                    <div className="space-y-6">
                      <h3 className="text-lg font-semibold">
                        Categorical Responses
                      </h3>
                      <div className="grid gap-6 md:grid-cols-2">
                        {Object.entries(
                          analyticsData.data.data.categoricalSummary
                        )
                          .sort(
                            ([, a], [, b]) =>
                              Object.keys(b).length - Object.keys(a).length
                          )
                          .map(([fieldName, fieldData]) => (
                            <Card
                              key={fieldName}
                              className="col-span-2 md:col-span-1"
                            >
                              <CardHeader>
                                <CardTitle className="text-base">
                                  {fieldName}
                                </CardTitle>
                              </CardHeader>
                              <CardContent className="space-y-4">
                                <ChartAnalysis
                                  fieldName={fieldName}
                                  data={fieldData}
                                  type="categorical"
                                />
                              </CardContent>
                            </Card>
                          ))}
                      </div>
                    </div>
                  )}

                  {/* Numeric Data Charts */}
                  {analyticsData?.data?.data?.numericSummary &&
                    Object.keys(analyticsData.data.data.numericSummary).length >
                      0 && (
                      <div className="space-y-6">
                        <h3 className="text-lg font-semibold">
                          Numeric Responses
                        </h3>
                        <div className="grid gap-6 md:grid-cols-2">
                          {Object.entries(
                            analyticsData.data.data.numericSummary
                          )
                            .sort(
                              ([, a], [, b]) =>
                                Object.keys(b).length - Object.keys(a).length
                            )
                            .map(([fieldName, fieldData]) => (
                              <Card key={fieldName}>
                                <CardHeader>
                                  <CardTitle className="text-base">
                                    {fieldName}
                                  </CardTitle>
                                </CardHeader>
                                <CardContent>
                                  <ChartAnalysis
                                    fieldName={fieldName}
                                    data={fieldData}
                                    type="numeric"
                                  />
                                </CardContent>
                              </Card>
                            ))}
                        </div>
                      </div>
                    )}

                  {analyticsData?.data?.data?.textSummary && (
                    <div className="space-y-6">
                      <h3 className="text-lg font-semibold">Word Cloud</h3>
                      <WordCloud data={analyticsData.data.data.textSummary} />
                    </div>
                  )}
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>
      ) : (
        <div className="text-center py-10">
          <h2 className="text-xl font-semibold text-muted-foreground">
            Select a form to view its analytics
          </h2>
        </div>
      )}
    </div>
  );
}

export default Dashboard;
